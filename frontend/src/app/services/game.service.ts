import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  baseUrl = environment.apiUrl + "/api/v1/game";
  startGameUrl = this.baseUrl + "/start";
  challengesUrl = this.baseUrl + "/challenges";
  answerUrl = this.baseUrl + "/answer";

  constructor(private client: HttpClient) { }

  start(request: StartGameRequest) {
    return this.client.post<StartGameResponse>(this.startGameUrl, request, {
      observe: 'response', withCredentials: true
    });
  }

  getChallenges(gameId: string) {
    return this.client.get<GetChallengesResponse>(this.challengesUrl + "?gameId=" + gameId, {
      observe: 'response', withCredentials: true
    });
  }

  answer(request: AnswerRequest) {
    return this.client.put<AnswerResponse>(this.challengesUrl + "?gameId=", request, {
      observe: 'response', withCredentials: true
    });

  }
}

export interface StartGameRequest {
  rated: boolean,
}

export interface StartGameResponse {
  gameId: string
}

export interface GetChallengesResponse {
  gameId: string,
  challenges: ChallengeView[]
}

export interface ChallengeView {
  id: number, texCode: string,
  hashedAnswer: string,
  gains: RatingGains[]
}

export interface RatingGains {
  start: number,
  end: number,
  gain: number
}

export interface AnswerRequest {
  gameId: string,
  answers: AnswerRequestEntry[]
}

export interface AnswerRequestEntry {
  challengeId: number,
  answer: string,
  seconds: number
}

export interface AnswerResponse {
  gameId: string,
  entries: AnswerResponseEntry[],
  ratingGain: number
}

export interface AnswerResponseEntry {
  challengeId: number,
  correct: boolean,
  ratingGain: number
}