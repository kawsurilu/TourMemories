import { NgModule } from '@angular/core';

import { TourMemoriesSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [TourMemoriesSharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [TourMemoriesSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class TourMemoriesSharedCommonModule {}
