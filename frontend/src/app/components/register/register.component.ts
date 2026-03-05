import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  firstName = '';
  lastName = '';
  username = '';
  password = '';
  error = '';
  loading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit(): void {
    this.error = '';
    this.loading = true;
    this.authService.register({
      username: this.username,
      password: this.password,
      firstName: this.firstName,
      lastName: this.lastName
    }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 409) {
          this.error = 'Username already exists. Please choose another.';
        } else if (err.status === 400) {
          this.error = 'Please fill in all fields correctly. Username must be 3+ characters, password 6+ characters.';
        } else if (err.status === 0) {
          this.error = 'Cannot reach the server. Please ensure the backend is running.';
        } else {
          this.error = 'Registration failed. Please try again.';
        }
      }
    });
  }
}
