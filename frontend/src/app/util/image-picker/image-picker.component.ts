import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';

@Component({
    selector: 'tt-image-picker',
    templateUrl: 'image-picker.component.html',
    styleUrls: ['./image-picker.component.scss'],
})
export class ImagePickerComponent implements OnInit, OnChanges {
    preview: string;
    loadPreview: boolean;
    @Input() defaultImage: string = undefined;
    @Input() size: number = 2048000;
    @Output() loading = new EventEmitter<boolean>(); // indicates loading                         
    @Output() image = new EventEmitter<string>(); // image with mime data and base64 encoded image as string

    constructor() { }

    ngOnInit() {
        if (this.defaultImage !== undefined) {
            this.preview = this.defaultImage;
            this.loadPreview = false;
        }
    }
    ngOnChanges() {
        this.preview = this.defaultImage;
        this.loadPreview = this.defaultImage === undefined;
    }

    encodeFile(fileInput: any) {
        if (fileInput.target.files && fileInput.target.files[0]) {
            if (fileInput.target.files[0].size > this.size ||
                ['image/jpeg', 'image/gif', 'image/png'].indexOf(fileInput.target.files[0].type) < 0) {
                alert('File too big!');
                this.preview = this.defaultImage;
                this.image.emit(undefined);
            } else {
                let reader = new FileReader();
                let self = this;

                reader.onload = function (e: any) {
                    self.preview = e.target.result;
                    self.image.emit(e.target.result);
                };
                reader.onloadstart = function (e: any) {
                    self.loadPreview = true;
                    self.loading.emit(true);
                };
                reader.onloadend = function (e: any) {
                    self.loadPreview = false;
                    self.loading.emit(false);
                };

                reader.readAsDataURL(fileInput.target.files[0]);
            }

        }

    }
    deleteIcon() {
        this.image.emit(undefined);
        this.preview = undefined;
    }
}
