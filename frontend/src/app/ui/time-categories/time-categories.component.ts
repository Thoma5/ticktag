import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {ActivatedRoute, Router} from '@angular/router';
import {AssignmentTagResultJson} from '../../api/model/AssignmentTagResultJson';
import {Observable} from 'rxjs';
import {Modal} from 'angular2-modal/plugins/bootstrap';
import {TimeCategoryJson} from '../../api/model/TimeCategoryJson';
import {TimecategoryApi} from '../../api/api/TimecategoryApi';

@Component({
  selector: 'tt-time-categories',
  templateUrl: './time-categories.component.html',
  styleUrls: ['./time-categories.component.scss'],
})
export class TimeCategoriesComponent implements OnInit {
  loading = true;
  projectId: string;
  cu = false;
  mode = '';
  toUpdateTimeCategory: TimeCategoryJson = undefined;
  private timeCategories: TimeCategoryJson[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private timeCategoryApi: TimecategoryApi,
    private apiCallService: ApiCallService,
    private modal: Modal) {
  }

  ngOnInit(): void {
    this.route.params
      .do(() => { this.loading = true; })
      .switchMap(params => {
        this.projectId = params['projectId'];
        return this.refresh();
      })
      .subscribe(() => {
        this.loading = false;
      });
  }
  private refresh(): Observable<void> {
    let timeCategories = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(h => this.timeCategoryApi
        .listProjectTimeCategoriesUsingGETWithHttpInfo(this.projectId, h));
    return Observable
      .zip(timeCategories)
      .do(tuple => {
        this.timeCategories = tuple[0];
      })
      .map(it => undefined)
      .catch(err => Observable.empty<void>());
  }

  onDeleteClicked(cat: TimeCategoryJson) {
    this.modal.confirm()
      .size('sm')
      .isBlocking(true)
      .showClose(false)
      .body('Are you sure you that you want to delete this item?')
      .okBtn('Delete')
      .open()
      .then(a => {
        a.result.then(result => {
          // Delete clicked
          this.apiCallService
            .call<any>(h => this.timeCategoryApi.deleteTimeCategoryUsingDELETEWithHttpInfo(cat.id, h))
            .subscribe(param => {
                this.refresh().subscribe();
              }
            );
        }).catch(result => {
          // Cancel clicked
        });
      });
  }

  onStartCreate() {
    this.mode = 'Create';
    this.cu = true;
  }

  onEditClicked(cat: TimeCategoryJson) {
    this.toUpdateTimeCategory = cat;
    this.cu = true;
    this.mode = 'Update';
  }

  onStopCreate() {
    this.cu = false;
  }

  finishCreateUpdate() {
    this.cu = false;
    this.refresh().subscribe();
  }
}
