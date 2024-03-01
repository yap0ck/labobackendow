import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Message} from "primeng/api";
import {TournamentService} from "../../services/tournament.service";
import {inTheFuture} from "../../validators/ValidatorsCustoms";
import {Categories} from "../../models/Tournament";

@Component({
  selector: 'app-create',
  templateUrl: './create.component.html',
  styleUrl: './create.component.css'
})
export class CreateComponent {
  form: FormGroup;
  messages: Message[]=[]
  categories: Categories[]=[
    Categories.JUNIOR,
    Categories.SENIOR,
    Categories.VETERAN
  ]

  constructor(private readonly _tournamentService:TournamentService,
              private readonly _formBuilder:FormBuilder) {
    this.form = this._formBuilder.group({
      name: this._formBuilder.control('',Validators.required),
      minTeam: this._formBuilder.control(null, [Validators.required, Validators.min(2), Validators.max(32)]),
      maxTeam: this._formBuilder.control(null, [Validators.required, Validators.min(2), Validators.max(32)]),
      minElo: this._formBuilder.control(null, [Validators.required, Validators.min(0), Validators.max(3000)]),
      maxElo: this._formBuilder.control(null, [Validators.required, Validators.min(0), Validators.max(3000)]),
      categories: this._formBuilder.control(''),
      isWomenOnly: this._formBuilder.control(''),
      startingDateTime: this._formBuilder.control('', inTheFuture())
    })
  }

  create(){
    console.log(this.form)
    this._tournamentService.create(this.form.value).subscribe(()=> this.messages=[{
      severity:"success",
      summary:"200"
    }])
  }
}
