import {throwError as observableThrowError, Observable} from 'rxjs';
import {Component, Input, OnInit} from '@angular/core';
import {Page} from './page.model';
import {PageService} from './page.service';
import * as SimpleMDE from 'simplemde';
import {catchError} from 'rxjs/operators';


@Component({
  selector: 'cf-page',
  templateUrl: './page.component.html',
  providers: [PageService]
})

export class PageComponent implements OnInit {
  @Input()
  title: string;

  page: Page;

  editMode = false;
  editor: SimpleMDE;

  constructor(private service: PageService) {

  }

  ngOnInit(): void {
    this.service.get(this.title)
      .pipe(
        catchError(error => {
          this.page = {id: this.title, content: ''};
          return observableThrowError(error);
        })
      )
      .subscribe(page => this.page = page);
  }

  edit() {
    this.editMode = true;
    setTimeout(() => {
      this.editor = new SimpleMDE();
      this.editor.value(this.page.content);
      this.editor.codemirror.on('change', () => {
        this.page.content = this.editor.value();
      });
    });
  }

  save() {
    this.service.save(this.page)
      .subscribe(() => this.editMode = false);
  }

  cancel() {
    this.editMode = false;
  }

}
