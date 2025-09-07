import {NgModule} from "@angular/core";
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "src/app/views/home/home.component";

export const routes: Routes = [
  {path: '', pathMatch: 'full', redirectTo: 'home'},
  {path: 'home', component: HomeComponent},
  {
    path: 'auth',
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./views/login/login.component').then(m => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./views/register/register.component').then(m => m.RegisterComponent),
      },
      {
        path: 'forgot-password',
        loadComponent: () =>
          import('./views/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent),
      },
      {path: '', pathMatch: 'full', redirectTo: 'register'},
    ],
  },

  // Protected shell
  /*
  {
    path: 'app',
    canActivate: [authGuard],
    // Optional shell/layout (topbar/sidebar); or skip component and route directly
    loadComponent: () =>
      import('./layout/app-shell/app-shell.component').then(m => m.AppShellComponent),
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(m => m.DashboardComponent),
        title: 'Dashboard • EasyRota',
      },
      {
        path: 'schedule',
        loadComponent: () =>
          import('./features/schedule/schedule.component').then(m => m.ScheduleComponent),
        title: 'Schedule • EasyRota',
      },
      {
        path: 'employees',
        canMatch: [roleGuard(['OWNER', 'ADMIN'])],
        loadComponent: () =>
          import('./features/employees/employees.component').then(m => m.EmployeesComponent),
        title: 'Employees • EasyRota',
      },
      {
        path: 'time-off',
        loadComponent: () =>
          import('./features/timeoff/timeoff.component').then(m => m.TimeOffComponent),
        title: 'Time Off • EasyRota',
      },
      {
        path: 'settings',
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./features/settings/settings-home.component').then(m => m.SettingsHomeComponent),
            title: 'Settings • EasyRota',
          },
          {
            path: 'profile',
            loadComponent: () =>
              import('./features/settings/profile/profile.component').then(m => m.ProfileComponent),
            title: 'Profile • EasyRota',
          },
          {
            path: 'business',
            canMatch: [roleGuard(['OWNER', 'ADMIN'])],
            loadComponent: () =>
              import('./features/settings/business/business.component').then(m => m.BusinessComponent),
            title: 'Business • EasyRota',
          },
          {
            path: 'team',
            canMatch: [roleGuard(['OWNER', 'ADMIN'])],
            loadComponent: () =>
              import('./features/settings/team/team.component').then(m => m.TeamComponent),
            title: 'Team • EasyRota',
          },
        ],
      },
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
    ],
  },
*/
  {
    path: 'not-found',
    loadComponent: () =>
      import('./views/errors/not-found/not-found.component').then(m => m.NotFoundComponent),
    title: 'Not Found • EasyRota',
  },

  // Catch-all
  {path: '**', redirectTo: 'not-found'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
