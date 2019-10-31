import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Partner} from '../../../pages/partners/shared/partner.model';
import {PartnerService} from '../../../pages/partners/shared/partner.service';
import {ActivatedRoute, Router} from '@angular/router';
import * as SimpleMDE from 'simplemde';

import {Location} from '@angular/common';

@Component({
  templateUrl: './partner-edit.component.html'
})
export class PartnerEditComponent implements OnInit {

  types = Partner.TYPES;
  editor: SimpleMDE;
  model: Partner = new Partner();
  submitted = false;
  @ViewChild('form') form: FormControl;

  constructor(private service: PartnerService,
              private router: Router,
              private route: ActivatedRoute,
              private location: Location) {
    const id = route.snapshot.params['id'];
    if (id) {
      this.service.getBy(id)
        .subscribe((partner) => {
          this.model = partner;
          this.editor.value(this.model.description);
        });
    }
  }

  ngOnInit(): void {
    this.editor = new SimpleMDE();
    this.editor.codemirror.on('change', () => {
      this.model.description = this.editor.value();
    });
  }


  save() {
    this.submitted = true;
    if (this.form.valid) {
      this.service.save(this.model)
        .subscribe(partner => {
          this.router.navigate(['/partners', partner.id]);
        });
    }
  }

  cancel() {
    this.location.back();
  }

}
