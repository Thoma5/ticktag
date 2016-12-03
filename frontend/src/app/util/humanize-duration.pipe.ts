import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

// See https://github.com/moment/moment/issues/348
moment.locale('precise-short-en', {
  relativeTime: {
    future: 'in\xa0%s',
    past: '%s\xa0ago',
    s: '%ds', // see https://github.com/timrwood/moment/pull/232#issuecomment-4699806
    m: '%dm',
    mm: '%dm',
    h: '%dh',
    hh: '%dh',
    d: '%dd',
    dd: '%dd',
    M: 'a\xa0month',
    MM: '%d\xa0months',
    y: 'a\xa0year',
    yy: '%d\xa0years'
  }
});


@Pipe({ name: 'ttHumanizeDuration' })
export class HumanizeDurationPipe implements PipeTransform {
  transform(value: number): string {
    if (value === 0 || value == null) {
      return '0s';
    }
    moment.locale('precise-short-en');
    return moment.duration(value, 'ms').humanize();
  }
}
