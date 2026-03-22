echo "============ start ==========="
pwd



for id in  "7622844c-4065-4272-a661-90da99cc1a8c" \
            "191f9c04-72b4-4f59-8f55-cd2e23f5c732" \
            "3c1887ad-2561-4678-abf8-17d9706c0d01" \
            "8f834105-1585-4051-93d5-3ce6f61325ad" \
            "2f111a87-8d0f-4861-b378-cb9df8cb747c" \
            "216d8640-4387-4b00-80ef-11657eb3d44e" \
            "23176155-4e7b-44f9-9d78-aff0c7dfe7a0" \
            "ab278e7c-e59a-4b6d-8c19-6a66e239f51f" \
            "f868002d-5281-4747-a81c-2c4ff5ae46b4" \
            "d374d03f-5122-4f02-9ac3-fa8df669c43f" \
            "9a68de66-3c1a-4238-99ae-c8b19875b006" \
            "6b8d6fd2-b835-4c51-a78d-4c988a80c58d" \
            "49c15baa-bdd6-4d62-b928-c9b7f7360ca9" \
            "cc40066a-54af-4316-98c4-f14ad03076ba" \
            "2c9a8b37-411d-44ce-97e5-5b96150bcd16" \
            "cd21c3b4-f80a-4927-b7f8-c286c3528370" \
            "607747bd-f752-46ca-8c57-9a7a882b35ff" \
            "52aa43f9-03e6-4c37-89ed-8dc9d645d2f8" \
            "a859937f-a1b2-4af3-b60a-5925888f0224" \
            "f15b5e90-42c9-42c3-b24c-82107041bdba" \
            "91e488f9-a014-442c-91ae-a2f683b6f09a" \
            "4a1b8e56-3b16-4a7b-b43a-bfd303b30d1b" \
            "a772d893-6462-4423-a511-5fec2f0b82a8" \
            "862eaef6-32b7-48bb-b3ba-ba31ce19a495" \
            "b443ab41-43fa-45a0-96bc-4ae4db51ecf2" \
            "f953198e-9472-4301-b3e9-5f47bb336f0e" \
            "a61d0fd1-27f8-47ef-8365-485cea8acce1" \
            "428bddd6-2c35-478a-a255-7db5b428c8d4" \
            "0cc7ff41-ab9e-40cb-bde8-d25e9214c716" \
            "a4f76848-872a-44e5-a95f-0468d1f4ea9a" \
            "76ee7373-420d-4c36-b51b-c720be1b5ed2" \
            "b8badba8-89b9-4f30-a63b-55b566d119f8"
do
  mkdir "$id"
  curl "https://2023.confitura.pl/api/users/$id/public" > "$id/public.json"
done
#
