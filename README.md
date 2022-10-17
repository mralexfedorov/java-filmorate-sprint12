# java-filmorate
## er-diagramm by dbdiagram.io
![filmorate](https://user-images.githubusercontent.com/95481854/196049841-952ec43a-cb9c-4f83-88d9-b3d78becfcc4.png)

## description by dbdiagram.io
```
Table public.users {
  user_id int [pk, increment]
  email varchar [not null]
  login varchar [not null]
  name varchar [default: 'common']
  birthday date [not null]
  
  Indexes {
    (user_id) [pk]
  }
}

Table public.user_friends {
  user_id int [ref: <> users.user_id]
  friend_id int [ref: <> users.user_id]
  friendship_status friendship_status
  
  Indexes {
    (user_id, friend_id) [pk]
  }
}

Table public.films {
  film_id int [pk, increment]
  name varchar [not null]
  description varchar [not null]
  release_date date [not null]
  duration int [not null]
  genre_id int [ref: > genres.genre_id]
  rating_id varchar [ref: > ratings.rating_id]
  
  Indexes {
    (film_id) [pk]
    (genre_id)
    (rating_id)
  }
}

Table public.film_likes {
  film_id int [ref: > films.film_id]
  user_id int [ref: > users.user_id]
  
  Indexes {
    (film_id, user_id) [pk]
  }
}

Table public.genres {
  genre_id int [pk, increment]
  name varchar [not null]
  
  Indexes {
    (genre_id) [pk]
  }
}

Table public.ratings {
  rating_id varchar [pk]
  description varchar [not null]
  
  Indexes {
    (rating_id) [pk]
  }  
}

Enum public.friendship_status {
  confirmed
  unconfirmed
}
```
