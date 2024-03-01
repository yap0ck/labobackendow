import { Component } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {LoginService} from "../../services/login.service";
import {inThePast} from "../../validators/ValidatorsCustoms";
import {Message} from "primeng/api";
import {InGameRole} from "../../models/User";
import {Subject} from "rxjs";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  form: FormGroup;
  messages: Message[]=[]
  private User: any;
  gender= [
    {name: "Homme", key: "0"},
    {name: "Femme", key: "1"},
    {name: "Autre", key: "2"}
  ];
  roles:InGameRole[] = []
  $destroyed= new Subject<boolean>

  constructor(private readonly _loginService:LoginService,
              private readonly _formBuilder: FormBuilder) {
    this.form= this._formBuilder.group({
      username: this._formBuilder.control('', Validators.required),
      email: this._formBuilder.control('', [Validators.required,Validators.email]),
      battleNet: this._formBuilder.control('', [Validators.required, Validators.pattern("(^([A-zÀ-ú][A-zÀ-ú0-9]{2,11})|(^([а-яёА-ЯЁÀ-ú][а-яёА-ЯЁ0-9À-ú]{2,11})))(#[0-9]{4,})$")]),
      password: this._formBuilder.control('', [Validators.required, Validators.pattern("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.{8,})")]),
      confirmedPassword: this._formBuilder.control('', Validators.required),
      dateOfBirth: this._formBuilder.control('', [Validators.required, inThePast()] ),
      gender: this._formBuilder.control('', Validators.required),
      ranking: this._formBuilder.control('', Validators.required),
      inGameRole: this._formBuilder.control('', Validators.required)
    },
      {validator: this.checkPasswords});
    this.roles = [
      this.InGameRole.TANK,
      this.InGameRole.SUPPORT,
      this.InGameRole.DPS
    ]
  }

  checkPasswords(group: FormGroup){
    let pass = group.get('password')?.value;
    let confirmedPassword = group.get('confirmedPassword')?.value;

    return pass === confirmedPassword ? null : {notSame:true}
  }

  create(){
    this._loginService.create(this.form.value).subscribe(()=> this.messages=[{
        severity: "success",
        summary:"200"
      }]
    )
  }

  protected readonly InGameRole = InGameRole;
}
