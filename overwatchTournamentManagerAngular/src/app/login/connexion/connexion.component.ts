import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Messages} from "primeng/messages";
import {Message} from "primeng/api";
import {LoginService} from "../../services/login.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-connexion',
  templateUrl: './connexion.component.html',
  styleUrl: './connexion.component.css'
})
export class ConnexionComponent implements OnInit{
  loginForm: FormGroup;
  messages: Message[]=[];
  constructor(private readonly _loginService: LoginService,
              private readonly _formBuilder: FormBuilder,
              private readonly _router: Router) {
    this.loginForm = this._formBuilder.group({
      username: this._formBuilder.control('', Validators.required),
      password: this._formBuilder.control('', Validators.required)
    })
  }

  ngOnInit() {
    this._loginService.logout()
  }

  login(){
    this._loginService.login(this.loginForm.value)
      .subscribe({
        next: (response) => {
          this._router.navigate(['home'])
        },
        error: (err) => {
          this.messages=[{
            severity: "error",
            summary: err.error.status,
            detail: err.error.message
          }]
        }
      })
  }
}


