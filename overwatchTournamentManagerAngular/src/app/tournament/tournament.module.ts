import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AllComponent } from './all/all.component';
import {RouterLink} from "@angular/router";
import {TournamentRoutingModule} from "./tournament-routing.module";
import {MessagesModule} from "primeng/messages";
import {TableModule} from "primeng/table";
import { CreateComponent } from './create/create.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {InputTextModule} from "primeng/inputtext";
import {InputNumberModule} from "primeng/inputnumber";
import {CheckboxModule} from "primeng/checkbox";
import {CalendarModule} from "primeng/calendar";
import {RippleModule} from "primeng/ripple";
import {DropdownModule} from "primeng/dropdown";
import {MultiSelectModule} from "primeng/multiselect";
import { OneComponent } from './one/one.component';
import { MatchComponent } from './one/match/match.component';



@NgModule({
  declarations: [
    AllComponent,
    CreateComponent,
    OneComponent,
    MatchComponent
  ],
    imports: [
        CommonModule,
        RouterLink,
        TournamentRoutingModule,
        MessagesModule,
        TableModule,
        ReactiveFormsModule,
        InputTextModule,
        InputNumberModule,
        CheckboxModule,
        CalendarModule,
        RippleModule,
        DropdownModule,
        MultiSelectModule,
        FormsModule
    ]
})
export class TournamentModule { }
