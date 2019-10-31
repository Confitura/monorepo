import {Pipe, PipeTransform} from '@angular/core';
import * as Marked from 'marked';

@Pipe({
  name: 'md'
})
export class MdPipe implements PipeTransform {

  transform(value: any, args?: any): any {
    return Marked(value, (error, result) => {
      return result;
    });
  }

}
