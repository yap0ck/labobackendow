import { NgModule } from '@angular/core';

import {RouterModule, Routes} from "@angular/router";
import {TournamentComponent} from "./tournament.component";
import {AllComponent} from "./all/all.component";
import {CreateComponent} from "./create/create.component";
import {ADMINONLY} from "../guard/admin-only.guard";
import {OneComponent} from "./one/one.component";

const routes:Routes =[{
  path: '',
  component: TournamentComponent,
  children:[
    {path: '', redirectTo: 'all', pathMatch:"full"},
    {path: 'all', component: AllComponent},
    {path: 'create', component: CreateComponent, canActivate: [ADMINONLY]},
    {path: 'one/:id', component:OneComponent}
  ]
}
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TournamentRoutingModule { }
