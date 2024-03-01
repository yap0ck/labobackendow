import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './shared/header/header.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import {MegaMenuModule} from "primeng/megamenu";
import { HomeComponent } from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { LoginComponent } from './login/login.component';
import {authInterceptor} from "./interceptors/auth.interceptor";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import { TournamentComponent } from './tournament/tournament.component';
import { TeamComponent } from './team/team.component';
import {ButtonModule} from "primeng/button";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    NavbarComponent,
    HomeComponent,
    LoginComponent,
    TournamentComponent,
    TeamComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        MegaMenuModule,
        HttpClientModule,
        BrowserAnimationsModule,
        ButtonModule
    ],
  providers: [
    {provide: 'apiUrl', useValue: "http://localhost:8081"},
    {provide: HTTP_INTERCEPTORS, useClass: authInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
