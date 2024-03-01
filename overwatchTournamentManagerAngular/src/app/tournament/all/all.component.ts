import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {TournamentShortDTO} from "../../models/Tournament";
import {Subject, takeUntil} from "rxjs";
import {TournamentService} from "../../services/tournament.service";
import {Messages} from "primeng/messages";
import {Message} from "primeng/api";
import {LoginService} from "../../services/login.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-all',
  templateUrl: './all.component.html',
  styleUrl: './all.component.css'
})
export class AllComponent implements OnInit, OnDestroy{
  tournament: TournamentShortDTO[]=[];
  messages: Message[]=[]

  $destroyed= new Subject<boolean>()

  isAdmin?: boolean;
  loginService= inject(LoginService)

  constructor(private readonly _tournamentService : TournamentService) {
    this.isAdmin = this.loginService.userConnected.value?.userRoles.includes("ADMIN")
  }
  ngOnInit() {
    this.getAll()
  }

  getAll(){
    this._tournamentService.getAll().pipe(takeUntil(this.$destroyed)).subscribe({
      next:(value)=> {
        if (value.body) {
          this.tournament = value.body
        }
      },
      error:(err)=> this.messages=[{
        severity: "Error",
        summary: err.error.status,
        detail: err.error.message
      }]
    })
  }

  delete(id: number){
    this._tournamentService.delete(id).pipe(takeUntil(this.$destroyed)).subscribe({
      error: (err) => this.messages=[{
        severity: "Error",
        summary: err.error.status,
        detail: err.error.message
      }],
      complete: () => {
        this.messages = [{
          severity: "success",
          detail: "Tournoi supprimé"
        }]
        this.getAll()
      }
    })

  }

  ngOnDestroy() {
      this.$destroyed.next(true);
      this.$destroyed.complete();
  }
}
