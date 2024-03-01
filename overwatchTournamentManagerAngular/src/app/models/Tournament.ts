export interface TournamentShortDTO{
  id: number,
  name: string,
  inscriptionNumber: number,
  minTeam: number,
  maxTeam: number,
  categories: Categories[],
  minElo: number,
  maxElo: number,
  status: Status,
  endingRegistration: Date,
  round: number
}

export interface TournamentCreateForm{
  name: string,
  minTeam: number,
  maxTeam: number,
  minElo: number,
  maxElo: number,
  categories: Categories[],
  isWomenOnly: boolean,
  startingDateTime: Date
}
export interface TournamentFullDTO{
  id: number,
  name: string,
  inscriptionNumber: number,
  minTeam: number,
  maxTeam: number,
  categories: Categories[],
  minElo: number,
  maxElo: number,
  status: Status,
  endingRegistration: Date,
  round: number,
  teamsId: number[]
}
export enum Categories{
  JUNIOR="JUNIOR",
  SENIOR="SENIOR",
  VETERAN="VETERAN"
}

export enum Status{
  REGISTRATION="Enregistrement en cours",
  IN_PROGRESS= "Tournoi en cours",
  FINISHED= "Terminé"
}
