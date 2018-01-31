import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {FormControl} from '@angular/forms';
import {Location} from '@angular/common';
import {User} from '../../core/user/user.model';
import {UserService} from '../../core/user/user.service';

@Component({
  templateUrl: './profile-edit.component.html',
  styleUrls: ['./profile-edit.component.scss']
})
export class ProfileEditComponent implements OnInit {
  isEdit = false;
  submitted = false;
  model: User = new User();
  @ViewChild('profileForm') form: FormControl;

  constructor(private service: UserService,
              private router: Router,
              private route: ActivatedRoute,
              private location: Location) {
  }

  ngOnInit(): void {
    this.route.params
      .subscribe((params: Params) => {
        const id = params['id'];
        if (id) {
          this.service.getBy(id)
            .subscribe(user => {
              this.model = user;
              this.isEdit = this.route.snapshot.params['id'] != null;
            });
        }

      });
  }

  save() {
    this.submitted = true;
    if (this.isValid()) {
      this.service.save(this.model)
        .subscribe(response => this.router.navigate([`/profile/${this.model.id}`]));
    }
  }

  isValid() {
    return this.form.valid;
  }


  cancel() {
    this.location.back();
  }
}
