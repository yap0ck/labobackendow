import { Component } from '@angular/core';
import {MegaMenuItem} from "primeng/api";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  items: MegaMenuItem[]=[
    {label:"Acceuil", routerLink:"home"},
    {label: "Tournoi", routerLink:'tournament'},
    {label: 'Team', routerLink:'team'},
    {icon: "pi pi-fw pi-user", routerLink:'login'}]
}
