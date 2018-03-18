import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {Presentation} from '../shared/presentation.model';
import {PresentationService} from '../shared/presentation.service';
import {Tag} from '../shared/tag.model';
import {FormControl} from '@angular/forms';
import {CurrentUser} from '../../core/security/current-user.service';
import {Location} from '@angular/common';

@Component({
  templateUrl: './presentation-edit.component.html',
  styleUrls: ['presentation-edit.component.scss']
})
export class PresentationEditComponent implements OnInit {
  languages = ['polish', 'english'];
  levels = ['basic', 'advanced', 'master'];
  owner: string = null;
  model = new Presentation();
  submitted = false;
  tags: Tag[] = [];

  @ViewChild('form') form: FormControl;

  constructor(private service: PresentationService,
              private user: CurrentUser,
              private route: ActivatedRoute,
              private router: Router,
              private location: Location) {
  }

  ngOnInit(): void {
    this.loadAllTags();
    this.route.params
      .subscribe((params: Params) => {
        this.getOwnerId(params);
        this.getPresentation(params);
      });

  }

  private getPresentation(params: Params) {
    const id = params['id'];
    if (id) {
      this.service.getOne(id)
        .subscribe(presentation => this.model = presentation);
    }
  }

  private getOwnerId(params: Params) {
    this.owner = params['owner'];
    if (this.owner == null) {
      this.owner = this.user.get().jti;
    }
  }

  private loadAllTags() {
    this.service.allTags()
      .subscribe(tags => this.tags = tags);
  }

  save() {
    this.submitted = true;
    if (this.form.valid) {
      this.service.save(this.owner, this.model)
        .subscribe(() => this.location.back());
    }
  }

  cancel() {
    this.location.back();
  }
}
