import {
    Directive, ElementRef, OnChanges, Input, SimpleChanges,
    AfterViewInit, NgZone
} from '@angular/core';

type ValidTarget = HTMLInputElement | HTMLTextAreaElement;

@Directive({
  selector: '[ttSelectAll]',
})
export class SelectAllDirective implements AfterViewInit, OnChanges {
  @Input() ttSelectAll: boolean;

  constructor(private elementRef: ElementRef, private zone: NgZone) { }

  ngAfterViewInit(): void {
    this.select();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ttSelectAll'] && changes['ttSelectAll'].currentValue === true) {
      this.select();
    }
  }

  private select(): void {
    let elem = this.elementRef.nativeElement as ValidTarget;
    this.zone.runOutsideAngular(() => {
      window.setTimeout(() => {
        elem.select();
      }, 0);
    });
  }
}
