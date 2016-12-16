import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {ApiCallService, ApiCallFn} from '..';

@Injectable()
export class ImagesService {
  private cache: { [key: string]: string|Observable<string> } = {};

  // TODO make readonly once Intellij supports readonly properties in ctr
  constructor(private apiCallService: ApiCallService) {
  }

  loadImage(key: string, call: ApiCallFn): Observable<string> {
    if (key in this.cache) {
      let value = this.cache[key];
      if (typeof value === 'string') {
        return Observable.of(value);
      } else {
        // A request is in flight
        return value;
      }
    } else {
      return this.cache[key] = this.apiCallService
        .callNoError<{ base64: string }>(call)
        .map(r => 'data:image/png;base64,' + r.base64)
        .do(data => { this.cache[key] = data; });
    }
  }
}
