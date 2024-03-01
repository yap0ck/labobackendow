import {Inject, Injectable} from '@angular/core';
import {BehaviorSubject, tap} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {AuthDto, LoginForm, UserCreateForm} from "../models/User";

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  userConnected = new BehaviorSubject<AuthDto|null>(null);

  constructor(private readonly _httpClient: HttpClient,
              @Inject('apiUrl') private _apiUrl: string) {
    if (localStorage.getItem("token")){
        this.userConnected.next({
          token: localStorage.getItem("token")!,
          userRoles: localStorage.getItem("role")!.split(","),
          username: localStorage.getItem("login")!
        });
      }
    }

  login(loginForm: LoginForm){
    return this._httpClient.post<AuthDto>(this._apiUrl+'/user/login', loginForm).pipe(
      tap(data => {
        localStorage.setItem("token", data.token)
        localStorage.setItem("role", data.userRoles.toString())
        localStorage.setItem("login", data.username)
        this.userConnected.next(data);
      })
    )
  }

  logout(){
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("login");
    this.userConnected.next(null)
  }

  create(form: UserCreateForm){
    return this._httpClient.post(this._apiUrl+'/user', form)
  }
}
