import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { ChallengeView, GameService, StartGameResponse } from '../services/game.service';
import { Subscription, switchMap } from 'rxjs';

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

        this.currentQuestion = this.challenges[0].texCode;
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
        this.counterDiv!.nativeElement.style.display = "none";
      }
      this.counter--;
    }, 500);

    this.addInputHandler();
  }

  addInputHandler() {
    document.addEventListener('keydown', (event: KeyboardEvent) => {
      var isNumber = /^\d$/;
      var isBackspace = event.key == 'Backspace';
      let value = this.answerInput!.nativeElement.value;

      if (isBackspace && value.length > 0) {
        value = value.substring(0, value.length - 1);

        this.answerInput!.nativeElement.value = value;
      } else if (value.length < this.MAX_DIGITS && isNumber.test(event.key)) {
        this.answerInput!.nativeElement.value += event.key;
      }
    });
  }

}


