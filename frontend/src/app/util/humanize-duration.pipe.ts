import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

// See https://github.com/moment/moment/issues/348
moment.locale('precise-short-en', {
  relativeTime: {
    future: 'in\xa0%s',
    past: '%s\xa0ago',
    s: '%d\xa0s', // see https://github.com/timrwood/moment/pull/232#issuecomment-4699806
    m: '%d\xa0m',
    mm: '%d\xa0m',
    h: '%d\xa0h',
    hh: '%d\xa0h',
    d: '%d\xa0d',
    dd: '%d\xa0d',
    M: 'a\xa0month',
    MM: '%d\xa0months',
    y: 'a\xa0year',
    yy: '%d\xa0years'
  }
});


@Pipe({ name: 'ttHumanizeDuration' })
export class HumanizeDurationPipe implements PipeTransform {
  transform(value: number): string {
    //   moment.locale('precise-short-en');
    //   return moment.duration(value, 'ms').humanize();
    var d = moment.duration(value, 'ms');
    return Math.floor(d.asHours())+":"+d.minutes();
  }
}
