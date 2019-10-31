import {RouterModule, Routes} from '@angular/router';
import {ProfileEditComponent} from './profile-edit/profile-edit.component';
import {ProfileViewComponent} from './profile-view/profile-view.component';
import {PresentationEditComponent} from './presentation-edit/presentation-edit.component';
import {ProfileCompleteGuard} from './shared/profile-complete-guard.service';
import {IsAuthenticatedGuard} from './shared/is-authenticated-guard.service';
import {CospeakersComponent} from './cospeakers/cospeakers.component';
import {NgModule} from '@angular/core';

const routes: Routes = [
  {path: '', component: ProfileViewComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard], canActivate: [IsAuthenticatedGuard]},
  {path: ':id', component: ProfileViewComponent, canActivate: [IsAuthenticatedGuard]},
  {path: ':id/edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard], canActivate: [IsAuthenticatedGuard]},
  {path: ':userId/presentation', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: ':userId/presentation/:id', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'presentation', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'presentation/:id', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'presentation/:id/cospeakers', component: CospeakersComponent, canActivate: [IsAuthenticatedGuard]},
];

@NgModule({
  imports: [
    RouterModule.forChild(
      routes
    )
  ],
  exports: [
    RouterModule
  ]
})
export class ProfileRoutingModule {
}
