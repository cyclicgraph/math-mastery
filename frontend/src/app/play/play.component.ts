import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { AnswerRequestEntry, AnswerResponse, AnswerResponseEntry, ChallengeView, GameService, StartGameResponse } from '../services/game.service';
import { Subscription, switchMap } from 'rxjs';
import md5Hex from 'md5-hex';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';

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
  answerResponse: AnswerResponse | null = null;
  dataSource = new MatTableDataSource<AnswerResponseEntry>([]);
  displayedColumns: string[] = ['challengeId', 'correct', 'ratingGain'];

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
      }
      this.counter--;
    }, 500);

    this.addInputHandler();
  }

  addInputHandler() {
    document.addEventListener('keydown', (event: KeyboardEvent) => {
      if (this.counter > 0 || this.answerResponse) return;

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
        // problem is that this can't be seen even for single second, when it's last digit of answer
        // don't really know why, if js function and view priority or what
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
            this.answerResponse = value.body;
            this.dataSource!.data = this.answerResponse!.entries;
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


function sleep(arg0: number) {
  throw new Error('Function not implemented.');
}

