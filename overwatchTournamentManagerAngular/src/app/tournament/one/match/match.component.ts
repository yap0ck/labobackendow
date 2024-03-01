import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {TournamentFullDTO} from "../../../models/Tournament";
import {MatchService} from "../../../services/match.service";
import {MatchDTO} from "../../../models/Match";
import {Message} from "primeng/api";
import {forkJoin, Subject} from "rxjs";
import {TeamFullDTO} from "../../../models/Team";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {LoginService} from "../../../services/login.service";

@Component({
  selector: 'app-match',
  templateUrl: './match.component.html',
  styleUrl: './match.component.css'
})
export class MatchComponent implements OnInit, OnDestroy{
  @Input() tournament!: TournamentFullDTO | null
  @Input() teams!: TeamFullDTO[] | null
  matches: MatchDTO[]=[]
  messages: Message[]=[]
  $destroyed= new Subject<boolean>()
  teamNames = new Map<number,string>
  matchFormGroup:MatchDTO[];
  isAdmin?: boolean;
  loginService= inject(LoginService)

  constructor(private readonly _matchService:MatchService,
              private _formBuilder:FormBuilder) {
    this.isAdmin = this.loginService.userConnected.value?.userRoles.includes("ADMIN")
    this.matchFormGroup = []
    }

  ngOnInit() {
    //this.matchForm = this._formBuilder.group(M)
    for (let i = 0; i < this.tournament!.round; i++) {
      this.getAll(i+1)

    }
  }

  getAll(round: number): void {
      this._matchService.getAll(this.tournament!.id, round)
          .subscribe( {
              next: (value)=> {
                this.matches = value;
                this.teams?.forEach((team) => {
                  this.teamNames.set(team.id, team.teamName)
                })
                this.matches?.forEach((match)=>{
                  this.matchFormGroup.push(match);
                })
              },
              error: err => this.messages=[{
                severity: "error",
                summary: err.error.status,
                detail: err.error.message,
            }]
          });
  }
  createMatchForm(match: MatchDTO): FormGroup {
    return this._formBuilder.group({
      scoreTeam1: match.scoreTeam1,
      scoreTeam2: match.scoreTeam2
    });
  }

  getFormGroup(id: number){
    return this.matchFormGroup[id]
  }


  onSubmit(id: number){
    let formMatch = this.createMatchForm(this.matchFormGroup[id])
    this._matchService.update(id+1, formMatch.value).subscribe({
      next: () => {
        this.messages = [{
          severity: "success",
          summary: "Success",
          detail: "Match updated successfully"
        }];
      },
      error: err => {
        this.messages = [{
          severity: "error",
          summary: err.error.status,
          detail: err.error.message,
        }];
      }
    });
  }

  ngOnDestroy() {
      this.$destroyed.next(true);
      this.$destroyed.complete();
  }
}
