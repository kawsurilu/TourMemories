import {Component, OnInit} from '@angular/core';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {FormBuilder, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {JhiAlertService, JhiDataUtils} from 'ng-jhipster';
import {IStatus, PinnedPostCategories, Status} from 'app/shared/model/status.model';
import {StatusService} from './status.service';
import {ILocation} from 'app/shared/model/location.model';
import {LocationService} from 'app/entities/location';
import {Account, AccountService} from "app/core";

@Component({
  selector: 'jhi-status-update',
  templateUrl: './status-update.component.html'
})
export class StatusUpdateComponent implements OnInit {
  isSaving: boolean;

  locations: ILocation[];
  statuses: IStatus[];
  pinnedStatuses: IStatus[];
  account: Account;

  editForm = this.fb.group({
    id: [],
    statusText: [null, [Validators.required]],
    privacy: [null, [Validators.required]],
    pinnedStatus: [null, [Validators.required]],
    locations: [null, [Validators.required]]
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected statusService: StatusService,
    protected locationService: LocationService,
    protected activatedRoute: ActivatedRoute,
    private accountService: AccountService,
    private router: Router,
    private fb: FormBuilder
  ) {
  }

  ngOnInit() {
    this.accountService.identity().then((account: Account) => {
      this.account = account;
      this.isSaving = false;
      this.getStatusByLoginId(this.account.login);
      this.activatedRoute.data.subscribe(({status}) => {
        this.updateForm(status);
      });
      this.locationService
        .query()
        .pipe(
          filter((mayBeOk: HttpResponse<ILocation[]>) => mayBeOk.ok),
          map((response: HttpResponse<ILocation[]>) => response.body)
        )
        .subscribe((res: ILocation[]) => (this.locations = res), (res: HttpErrorResponse) => this.onError(res.message));
    });
  }

  updateForm(status: IStatus) {
    this.editForm.patchValue({
      id: status.id,
      statusText: status.statusText,
      privacy: status.privacy,
      pinnedStatus: status.pinnedStatus,
      loginId: status.loginId,
      locations: status.locations
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file = event.target.files[0];
        if (isImage && !/^image\//.test(file.type)) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      () => console.log('blob added'), // sucess
      this.onError
    );
  }

  getStatusByLoginId(loginId: string) {
    this.statuses = [];
    this.pinnedStatuses = [];
    this.statusService.getByLoginId(this.accountService.getLoginId())
      .pipe(
        filter((res: HttpResponse<IStatus[]>) => res.ok),
        map((res: HttpResponse<IStatus[]>) => res.body)
      )
      .subscribe(
        (res: IStatus[]) => {
          for(let i = 0; i < res.length; i++) {
            if(res[i].pinnedStatus.toString() === PinnedPostCategories.PINNED.toString())
              this.pinnedStatuses.push(res[i]);
            else
              this.statuses.push(res[i]);
          }
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  previousState() {
    this.router.navigate(['/status/new']);
  }

  save() {
    this.isSaving = true;
    const status = this.createFromForm();
    if (status.id !== undefined) {
      this.subscribeToSaveResponse(this.statusService.update(status));
    } else {
      this.subscribeToSaveResponse(this.statusService.create(status));
    }
  }

  private createFromForm(): IStatus {
    return {
      ...new Status(),
      id: this.editForm.get(['id']).value,
      statusText: this.editForm.get(['statusText']).value,
      privacy: this.editForm.get(['privacy']).value,
      pinnedStatus: this.editForm.get(['pinnedStatus']).value,
      loginId: this.account.login,
      locations: this.editForm.get(['locations']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStatus>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.editForm.reset();
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackLocationById(index: number, item: ILocation) {
    return item.id;
  }
}
