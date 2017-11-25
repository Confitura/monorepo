import {RouterModule, Routes} from '@angular/router';
import {ModuleWithProviders} from '@angular/core';
import {ProfileEditComponent} from './profile-edit/profile-edit.component';
import {ProfileViewComponent} from './profile-view/profile-view.component';
import {PresentationEditComponent} from './presentation-edit/presentation-edit.component';
import {ProfileCompleteGuard} from './shared/profile-complete-guard.service';
import {IsAuthenticatedGuard} from './shared/is-authenticated-guard.service';

const appRoutes: Routes = [
  {path: 'profile', component: ProfileViewComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'profile/edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard], canActivate: [IsAuthenticatedGuard]},
  {path: 'profile/:id/edit', component: ProfileEditComponent, canDeactivate: [ProfileCompleteGuard], canActivate: [IsAuthenticatedGuard]},
  {path: 'profile/:id', component: ProfileViewComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'presentation', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'user/:userId/presentation', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'presentation/:id', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
  {path: 'user/:userId/presentation/:id', component: PresentationEditComponent, canActivate: [IsAuthenticatedGuard]},
];
export const routing: ModuleWithProviders = RouterModule.forRoot(appRoutes);
