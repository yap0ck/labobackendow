import {Component, inject} from '@angular/core';
import {LoginService} from "../services/login.service";

@Component({
  selector: 'app-tournament',
  templateUrl: './tournament.component.html',
  styleUrl: './tournament.component.css'
})
export class TournamentComponent {
  isAdmin?: boolean;
  loginService= inject(LoginService)

  constructor() {
    this.isAdmin = this.loginService.userConnected.value?.userRoles.includes("ADMIN")
  }
}
