import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { TourMemoriesSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [TourMemoriesSharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [TourMemoriesSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TourMemoriesSharedModule {
  static forRoot() {
    return {
      ngModule: TourMemoriesSharedModule
    };
  }
}
