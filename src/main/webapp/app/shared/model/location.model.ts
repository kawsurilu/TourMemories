export interface ILocation {
  id?: number;
  location?: string;
}

export class Location implements ILocation {
  constructor(public id?: number, public location?: string) {}
}
