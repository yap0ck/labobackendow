import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {Status, TournamentFullDTO} from "../../models/Tournament";
import {TournamentService} from "../../services/tournament.service";
import {ActivatedRoute, Router} from "@angular/router";
import {map, Subject, takeUntil} from "rxjs";
import {LoginService} from "../../services/login.service";
import {Message} from "primeng/api";
import {TeamService} from "../../services/team.service";
import {TeamFullDTO} from "../../models/Team";

@Component({
  selector: 'app-one',
  templateUrl: './one.component.html',
  styleUrl: './one.component.css'
})
export class OneComponent implements OnInit, OnDestroy{
  tournament!: TournamentFullDTO | null
  $destroyed= new Subject<boolean>()
  messages: Message[]=[]
  tournamentstarted: boolean = false
  teams: TeamFullDTO[]=[]
  isStarted= false


  isAdmin?: boolean;
  loginService= inject(LoginService)

  constructor(private readonly _tournamentService: TournamentService,
              private readonly _activatedRoute: ActivatedRoute,
              private readonly _router: Router,
              private readonly _teamService: TeamService) {
    this.isAdmin = this.loginService.userConnected.value?.userRoles.includes("ADMIN")
    }
  ngOnInit(): void {
    this._tournamentService.getById(this._activatedRoute.snapshot.params['id']).pipe(
      map(response => response.body)
    ).subscribe({
      next:(value)=> {
        this.tournament = value;
        if(this.tournament?.teamsId){
          this.getTeams(this.tournament.teamsId)

      }
    },
    error:(err)=> console.log(err.error.message)
    })
  }

  ngOnDestroy(): void {
      this.$destroyed.next(true);
      this.$destroyed.complete();
  }

  delete(){
      if (this.tournament?.id) {
        this._tournamentService.delete(this.tournament.id).pipe(takeUntil(this.$destroyed)).subscribe({
          error: (err) => this.messages = [{
            severity: "Error",
            summary: err.error.status,
            detail: err.error.message
          }],
          complete: () => {
            this.messages = [{
              severity: "success",
              detail: "Tournoi supprimé"
            }]
            this._router.navigate(["/tournament/all"])
          }
        })
      }
    }


  register(){
      this._tournamentService.register(this.tournament!.id).pipe(takeUntil(this.$destroyed)).subscribe({
        error: (err) => this.messages = [{
          severity: "Error",
          summary: err.error.status,
          detail: err.error.message
        }],
        complete: () => {
          this.messages = [{
            severity: "success",
            detail: "Inscription réussie"
          }]
        }
      })

  }

  unregister(){
      this._tournamentService.unregister(this.tournament!.id).pipe(takeUntil(this.$destroyed)).subscribe({
        error: (err) => this.messages = [{
          severity: "Error",
          summary: err.error.status,
          detail: err.error.message
        }],
        complete: () => {
          this.messages = [{
            severity: "success",
            detail: "Désinscription réussie"
          }]
        }
      })

    }

    start(){
        this._tournamentService.start(this.tournament!.id).pipe(takeUntil(this.$destroyed)).subscribe({
          error: (err) => this.messages = [{
            severity: "Error",
            summary: err.error.status,
            detail: err.error.message
          }],
          complete: () => {
            this.tournamentstarted=true
            this.messages = [{
              severity: "success",
              detail: "Tournoi démarré"
            }]
          }
        })
    }

    getTeams(ids: number[]){
      if(this.tournament!.status.includes("IN_PROGRESS")) this.isStarted= true
      this._teamService.getOne(ids).pipe(takeUntil(this.$destroyed)).subscribe({
        next:(value) => {
          this.teams = value;

        },
        error: (err) => this.messages = [{
          severity: "Error",
          summary: err.error.status,
          detail: err.error.message
        }],
        }
      )
    }

  protected readonly Status = Status;
}
