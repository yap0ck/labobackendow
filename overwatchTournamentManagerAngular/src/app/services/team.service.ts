import {Inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TeamFullDTO} from "../models/Team";

@Injectable({
  providedIn: 'root'
})
export class TeamService {

  constructor(private readonly _httpCclient: HttpClient,
              @Inject('apiUrl')private readonly _apiUrl: string ) { }

  getOne(ids:number[]){
    return this._httpCclient.post<TeamFullDTO[]>(this._apiUrl+'/team/allbyid', ids)
  }


}
