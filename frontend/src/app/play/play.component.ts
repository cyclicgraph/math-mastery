import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AnswerRequestEntry, ChallengeView, GameService, StartGameResponse } from '../services/game.service';
import { Subscription, switchMap } from 'rxjs';
import md5Hex from 'md5-hex';

@Component({
  selector: 'app-play',
  templateUrl: './play.component.html',
  styleUrls: ['./play.component.css']
})
export class PlayComponent implements AfterViewInit {
  counter = 3;
  @ViewChild('counterText') counterSpan?: ElementRef;
  @ViewChild('div') counterDiv?: ElementRef;
  @ViewChild('answerInput') answerInput?: ElementRef;
  MAX_DIGITS = 4;
  gameId?: string;
  challenges: ChallengeView[] = [];
  currentQuestion = "";
  currentChallenge: ChallengeView | null = null;
  currentChallengeIndex = -1;
  answers: AnswerRequestEntry[] = [];

  // first initialization won't be needed
  questionDisplayedDate = new Date();
  questionAnsweredDate = new Date();

  private subscription = new Subscription();

  constructor(private _gameService: GameService) {
    this.startGame();
  }

  private startGame(): void {
    const gameStartSubscription = this._gameService.start({ "rated": true }).pipe(
      switchMap(v => {
        this.gameId = v.body?.gameId;
        return this._gameService.getChallenges(this.gameId!);
      })
    ).subscribe({
      next: v => {
        this.challenges =
          v.body!.challenges

        this.showNextQuestion();
      },
      error: v => this.handleError(v)
    });

    this.subscription.add(gameStartSubscription);
  }

  private handleError(error: any): void {
    if (this.counterDiv && this.counterDiv.nativeElement) {
      this.counterDiv.nativeElement.text = "error occurred";
    }
    console.log(error);
  }

  ngAfterViewInit(): void {
    // disabled to handle input only with global eventListener
    this.answerInput!.nativeElement.disabled = true;

    const interval = setInterval(() => {
      if (this.counter <= 0) {
        clearInterval(interval);
      }

      if (this.counter == 1) {
        this.questionDisplayedDate = new Date();
        this.counterDiv!.nativeElement.style.display = "none";
      }
      this.counter--;
    }, 111);

    this.addInputHandler();
  }

  addInputHandler() {
    document.addEventListener('keydown', (event: KeyboardEvent) => {
      console.log(this.counter);
      if (this.counter > 0) return;

      this.questionAnsweredDate = new Date();
      var isNumber = /^\d$/;
      var isBackspace = event.key == 'Backspace';
      let value = this.answerInput!.nativeElement.value;

      if (isBackspace && value.length > 0) {
        value = value.substring(0, value.length - 1);

        this.answerInput!.nativeElement.value = value;
        // was tested before, return
        return;
      } else if (value.length < this.MAX_DIGITS && isNumber.test(event.key)) {
        this.answerInput!.nativeElement.value += event.key;
      } else {
        // value was not changed so it was tested before
        return;
      }

      this.testAnswer(this.answerInput!.nativeElement.value);
    });
  }

  testAnswer(value: string) {
    let isCorrect = md5Hex(value) == this.currentChallenge?.hashedAnswer;
    console.log(isCorrect);

    if (isCorrect) {
      // save in history
      var secondsToAnswer = (this.questionAnsweredDate.getTime() - this.questionDisplayedDate.getTime()) / 1000;
      this.answers.push({
        "answer": value,
        "challengeId": this.currentChallenge!.id,
        "seconds": secondsToAnswer
      });

      // if it was last question, send answers to backend
      if (this.challenges.length - 1 == this.currentChallengeIndex) {
        this._gameService.answer({
          "answers": this.answers,
          "gameId": this.gameId!
        }).subscribe({
          next: value => {
            console.log(value.body);
          }
        });
      } else {
        this.showNextQuestion();
      }
    }
  }

  showNextQuestion() {
    this.currentChallengeIndex++;
    this.currentChallenge = this.challenges[this.currentChallengeIndex];
    this.answerInput!.nativeElement.value = '';
    this.currentQuestion = this.currentChallenge.texCode;
    this.questionDisplayedDate = new Date();
  }
}


