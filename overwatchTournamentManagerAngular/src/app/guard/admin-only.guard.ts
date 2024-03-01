import { CanActivateFn } from '@angular/router';
import {inject} from "@angular/core";
import {LoginService} from "../services/login.service";

export const ADMINONLY: CanActivateFn = (route, state) => {
  const loginService= inject(LoginService)

  if (loginService.userConnected.value?.userRoles.includes("ADMIN")){
    return true;
  }
  return false
};
