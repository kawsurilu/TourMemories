import { Routes } from '@angular/router';

import { ErrorComponent } from './error.component';

export const errorRoute: Routes = [
  {
    path: 'error',
    component: ErrorComponent,
    data: {
      authorities: [],
      pageTitle: 'TourMemories'
    }
  },
  {
    path: 'accessdenied',
    component: ErrorComponent,
    data: {
      authorities: [],
      pageTitle: 'TourMemories',
      error403: true
    }
  },
  {
    path: '404',
    component: ErrorComponent,
    data: {
      authorities: [],
      pageTitle: 'TourMemories',
      error404: true
    }
  },
  {
    path: '**',
    redirectTo: '/404'
  }
];
