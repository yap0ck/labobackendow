import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ConnexionComponent } from './connexion/connexion.component';
import {RouterLink} from "@angular/router";
import {ReactiveFormsModule} from "@angular/forms";
import {MessagesModule} from "primeng/messages";
import {PasswordModule} from "primeng/password";
import {LoginRoutingModule} from "./login-routing.module";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {RippleModule} from "primeng/ripple";
import { RegisterComponent } from './register/register.component';
import {DividerModule} from "primeng/divider";
import {CalendarModule} from "primeng/calendar";
import {RadioButtonModule} from "primeng/radiobutton";
import {ListboxModule} from "primeng/listbox";
import {DropdownModule} from "primeng/dropdown";



@NgModule({
  declarations: [
    ConnexionComponent,
    RegisterComponent
  ],
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
    MessagesModule,
    PasswordModule,
    LoginRoutingModule,
    ButtonModule,
    InputTextModule,
    RippleModule,
    DividerModule,
    CalendarModule,
    RadioButtonModule,
    ListboxModule,
    DropdownModule
  ]
})
export class LoginModule { }
