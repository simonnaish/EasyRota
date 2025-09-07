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
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.scss'],
  imports: [
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    TranslatePipe,
    MatButton,
    RouterLink,
    NgIf
  ],
  standalone: true
})
export class ForgotPasswordComponent implements OnInit {
  private readonly _fb: FormBuilder = inject(FormBuilder);
  private readonly _authService: AuthService = inject(AuthService);
  public forgotPasswordForm: FormGroup | undefined;

  public ngOnInit(): void {
    this.initForgotPasswordForm();
  }

  private initForgotPasswordForm(): void {
    this.forgotPasswordForm = this._fb.group({
      'email': ['aaaaa@aaaaa', [Validators.required, Validators.email]]
    })
  }

  public onSubmit(): void {
    console.log(this.forgotPasswordForm?.value);
    let subscription: Subscription;
    subscription = this._authService.forgotPassword(this.forgotPasswordForm?.get('email')?.value).subscribe({
      next: (response) => console.log(response),
      error: (e) => {
        console.log(e);
        subscription.unsubscribe();
      },
      complete: () => subscription.unsubscribe()
    });
  }

}
