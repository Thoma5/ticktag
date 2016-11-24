import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'ttJson' })
export class JsonPipe implements PipeTransform {
    transform(value: any): string {
        return JSON.stringify(value);
    }
}
