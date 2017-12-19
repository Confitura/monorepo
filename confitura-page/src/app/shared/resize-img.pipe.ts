import {Pipe, PipeTransform} from '@angular/core';
import {environment} from '../../environments/environment';

@Pipe({
  name: 'resizeImg'
})
export class ResizeImgPipe implements PipeTransform {

  transform(value: string, width?: number): string {
    if (value && environment.production) {
      return value.replace('/photos/', `/photos/${width || 350}/`);
    }
    return value;
  }

}
