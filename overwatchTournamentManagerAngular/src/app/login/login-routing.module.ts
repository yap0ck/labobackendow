import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {LoginComponent} from "./login.component";
import {ConnexionComponent} from "./connexion/connexion.component";
import {RegisterComponent} from "./register/register.component";

const routes: Routes = [
  {path: '',
  component: LoginComponent,
  children:[
    {path: '', redirectTo: 'connexion', pathMatch: "full"},
    {path: 'connexion', component: ConnexionComponent},
    {path: 'register', component: RegisterComponent}
  ]}
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoginRoutingModule { }
