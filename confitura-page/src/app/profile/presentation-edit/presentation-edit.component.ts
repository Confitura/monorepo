import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Presentation} from '../shared/presentation.model';
import {PresentationService} from '../shared/presentation.service';
import {Tag} from '../shared/tag.model';
import {FormControl} from '@angular/forms';
import {CurrentUser} from '../../core/security/current-user.service';
import {Location} from '@angular/common';
import {Observable} from 'rxjs/Observable';
import {flatMap, map} from 'rxjs/operators';

@Component({
  templateUrl: './presentation-edit.component.html'
})
export class PresentationEditComponent implements OnInit {
  userId: string = null;
  model = new Presentation();
  submitted = false;

  constructor(private service: PresentationService,
              private user: CurrentUser,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) {
  }

  ngOnInit(): void {
    this.route.params
      .subscribe((params: Params) => {
        this.userId = params['userId'];
        if (this.userId == null) {
          this.userId = this.user.get().jti;
        }
        const id = params['id'];
        if (id) {
          this.service.getOne(id)
            .pipe(
              flatMap((presentation: Presentation) => this.service.getCospeakers(id)
                .pipe(
                  map(it => {
                    presentation.cospeakers = it;
                    return presentation;
                  })))
            )
            .subscribe((presentation: Presentation) => this.model = presentation);
        }
      });

  }

  save(form: FormControl) {
    this.submitted = true;
    if (form.valid) {
      this.service.save(this.userId, this.model)
        .subscribe(() => this.location.back());
    }
  }

  cancel() {
    this.router.navigate([`/profile/${this.userId}`]);
  }

  public tags = (): Observable<Tag[]> => {
    return this.service.allTags();
  }
}
