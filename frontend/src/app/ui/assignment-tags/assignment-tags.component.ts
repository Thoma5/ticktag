import {Component, OnInit} from '@angular/core';
import {ApiCallService} from '../../service';
import {AssignmenttagApi} from '../../api/api/AssignmenttagApi';
import {ActivatedRoute, Router} from '@angular/router';
import {AssignmentTagResultJson} from '../../api/model/AssignmentTagResultJson';
import {Observable} from 'rxjs';

@Component({
  selector: 'tt-assignment-tags',
  templateUrl: './assignment-tags.component.html',
  styleUrls: ['./assignment-tags.component.scss'],
})
export class AssignmentTagsComponent implements OnInit {
  loading = true;
  projectId: string;
  cu = false;
  mode = '';
  toUpdateAssignmentTag: AssignmentTagResultJson = undefined;
  private assignmentTags: AssignmentTagResultJson[] = [];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private assignmentTagApi: AssignmenttagApi,
    private apiCallService: ApiCallService) {
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
    let assignmentTags = this.apiCallService
      .callNoError<AssignmentTagResultJson[]>(h => this.assignmentTagApi
        .listAssignmentTagsUsingGETWithHttpInfo(this.projectId, h));
    return Observable
      .zip(assignmentTags)
      .do(tuple => {
        this.assignmentTags = tuple[0];
      })
      .map(it => undefined)
      .catch(err => Observable.empty<void>());
  }

  onDeleteClicked(tag: AssignmentTagResultJson) {
    this.apiCallService
      .call<any>(h => this.assignmentTagApi.deleteAssignmentTagUsingDELETEWithHttpInfo(tag.id, h))
      .subscribe(param => {
          this.refresh().subscribe();
        }
      );
  }

  onStartCreate() {
    this.mode = 'Create';
    this.cu = true;
  }

  onEditClicked(tag: AssignmentTagResultJson) {
    this.toUpdateAssignmentTag = tag;
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
