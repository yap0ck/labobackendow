import {Inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {TournamentCreateForm, TournamentFullDTO, TournamentShortDTO} from "../models/Tournament";

@Injectable({
  providedIn: 'root'
})
export class TournamentService {

  constructor(private readonly _httpClient: HttpClient,
              @Inject('apiUrl') private _apiUrl: string) { }

  getAll():Observable<HttpResponse<TournamentShortDTO[]>>{
    return this._httpClient.get<TournamentShortDTO[]>(this._apiUrl+'/tournament', {observe: "response"})
  }
  create(form: TournamentCreateForm){
    return this._httpClient.post(this._apiUrl+'/tournament', form)
  }

  delete(id: number){
    return this._httpClient.delete(this._apiUrl+'/tournament/'+id)
  }

  getById(id: number): Observable<HttpResponse<TournamentFullDTO>> {
      return this._httpClient.get<TournamentFullDTO>(this._apiUrl+'/tournament/'+id, {observe: "response"});
  }

  register(id:number){
    return this._httpClient.post(this._apiUrl+'/tournament/register/' + id, {observe: "response"})
  }

  unregister(id:number){
    return this._httpClient.delete(this._apiUrl+'/tournament/unregister/'+id)
  }

  start(id:number){
    return this._httpClient.put(this._apiUrl+'/tournament/start/'+id, {observe: "response"})
  }
}
