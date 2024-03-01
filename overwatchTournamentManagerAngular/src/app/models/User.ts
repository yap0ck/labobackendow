export interface LoginForm{
  username: string,
  password: string
}

export interface AuthDto{
  token: string,
  username: string,
  userRoles: string[]
}

export interface UserCreateForm{
  username: string,
  email: string,
  battleNet: string,
  password: string,
  confirmedPassword: string,
  dateOfBirth: Date,
  gender: Gender,
  ranking: string,
  inGameRole: InGameRole[]
}
enum UserRole{
  ADMIN,
  MODERATOR,
  PLAYER
}

export enum InGameRole{
  TANK= "TANK",
  DPS= "DPS",
  SUPPORT= "SUPPORT"
}

export enum Gender{
  HOMME='Homme',
  FEMME='Femme',
  AUTRE='Autre'
}
