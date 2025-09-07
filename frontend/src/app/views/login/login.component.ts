import {NgIf} from "@angular/common";
import {Component, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {RouterLink} from "@angular/router";
import {TranslatePipe} from "@ngx-translate/core";
import {Subscription} from "rxjs";
import {AuthService} from "src/app/services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  imports: [
    ReactiveFormsModule,
    NgIf,
    MatFormField,
    MatInput,
    MatLabel,
    TranslatePipe,
    MatButton,
    RouterLink
  ],
  standalone: true
})
export class LoginComponent implements OnInit {
  private readonly _fb: FormBuilder = inject(FormBuilder);
  private readonly _authService: AuthService = inject(AuthService);
  public userForm: FormGroup | undefined;

  public ngOnInit(): void {
    this.initUserForm();
  }

  private initUserForm(): void {
    this.userForm = this._fb.group({
      'email': ['aaaaa@aaaaa', [Validators.required, Validators.email]],
      'password': ['12345', [Validators.required]],

    })
  }

  public onSubmit(): void {
    let subscription: Subscription;
    subscription = this._authService.login(this.userForm?.value).subscribe({
      next: (response) => console.log(response),
      error: (e) => {
        console.log(e);
        subscription.unsubscribe();
      },
      complete: () => subscription.unsubscribe()
    })
  }

}
