import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'tt-image-picker',
    templateUrl: 'image-picker.component.html',
    styleUrls: ['./image-picker.component.scss'],
})
export class ImagePickerComponent {
    preview: string;
    loadPreview: boolean;
    @Input() size: number;
    @Output() loading = new EventEmitter<boolean>(); // indicates loading                         
    @Output() image = new EventEmitter<string>(); // image with mime data and base64 encoded image as string

    constructor() {
    }

    encodeFile(fileInput: any) {
        console.log(fileInput)
        if (fileInput.target.files && fileInput.target.files[0]) {
            if (fileInput.target.files[0].size > 2048000 ||
            ['image/jpeg', 'image/gif', 'image/png'].indexOf(fileInput.target.files[0].type) < 0) {
                alert('File too big!');
                this.preview = undefined;
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
    deleteIcon(){
        this.image.emit(undefined);
        this.preview = undefined;
    }
}
