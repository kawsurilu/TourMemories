import {Component, OnInit} from '@angular/core';
import {NgbModalRef} from '@ng-bootstrap/ng-bootstrap';
import {JhiAlertService, JhiEventManager} from 'ng-jhipster';

import {Account, AccountService, LoginModalService} from 'app/core';
import {filter, map} from "rxjs/operators";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {IStatus, PrivacyCategories} from "app/shared/model/status.model";
import {StatusService} from "app/entities/status";

@Component({
  selector: 'jhi-home',
  templateUrl: './home.component.html',
  styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
  statuses: IStatus[];
  account: Account;
  modalRef: NgbModalRef;

  constructor(
    private accountService: AccountService,
    private loginModalService: LoginModalService,
    private eventManager: JhiEventManager,
    protected statusService: StatusService,
    protected jhiAlertService: JhiAlertService,
  ) {
  }

  ngOnInit() {
    this.getStatuses();
    this.accountService.identity().then((account: Account) => {
      this.account = account;
    });
    this.registerAuthenticationSuccess();
  }

  registerAuthenticationSuccess() {
    this.eventManager.subscribe('authenticationSuccess', message => {
      this.accountService.identity().then(account => {
        this.account = account;
      });
    });
  }

  isAuthenticated() {
    return this.accountService.isAuthenticated();
  }

  login() {
    this.modalRef = this.loginModalService.open();
  }

  getStatuses() {
    this.statuses = [];
    this.statusService
      .getByPrivacy(PrivacyCategories.PUBLIC)
      .pipe(
        filter((res: HttpResponse<IStatus[]>) => res.ok),
        map((res: HttpResponse<IStatus[]>) => res.body)
      )
      .subscribe(
        (res: IStatus[]) => {
          this.statuses = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
