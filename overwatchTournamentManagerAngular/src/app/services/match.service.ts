import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {MatchDTO, MatchForm} from "../models/Match";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  constructor(private readonly _httpClient: HttpClient,
              @Inject('apiUrl') private _apiUrl: string) { }

  getAll(id:number, round:number):Observable<MatchDTO[]>{
    return this._httpClient.get<MatchDTO[]>(this._apiUrl+'/match/'+id+'/'+round)
  }

  update(id: number, form: MatchForm){
    console.log(id)
    console.log(form)
    return this._httpClient.put(this._apiUrl+'/match/'+id, form)
  }
}
