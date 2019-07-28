import { ILocation } from 'app/shared/model/location.model';

export const enum PrivacyCategories {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE'
}

export const enum PinnedPostCategories {
  PINNED = 'PINNED',
  UNPINNED = 'UNPINNED'
}

export interface IStatus {
  id?: number;
  statusText?: any;
  privacy?: PrivacyCategories;
  pinnedStatus?: PinnedPostCategories;
  loginId?: string;
  locations?: ILocation;
}

export class Status implements IStatus {
  constructor(
    public id?: number,
    public statusText?: any,
    public privacy?: PrivacyCategories,
    public pinnedStatus?: PinnedPostCategories,
    public loginId?: string,
    public locations?: ILocation
  ) {}
}
