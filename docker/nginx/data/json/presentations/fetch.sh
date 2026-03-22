

cd "docker/nginx/data/json/presentations"


for id in "16ee6605-4da6-4dfe-bf7f-f5204e522741" \
  "16ee6605-4da6-4dfe-bf7f-f5204e522741" \
  "4d757797-956c-44c8-b60d-4d6d674d4c14" \
  "7edf1184-f4c9-4e8f-a992-2e769a56ce91" \
  "8de02165-3d97-42bb-9aae-1e0207216da8" \
  "ecd2b3f8-bf42-46a2-8935-210c6176bc52" \
  "1dee35aa-aac3-4727-b568-1429da549dd5" \
  "eccc46ae-522e-478f-b4e6-c626f713e844" \
  "4f579c17-428b-41d6-94dc-b7deb203f687" \
  "540af997-c327-403b-b8b1-595c95c3d91c" \
  "8d9bc146-4dee-48f2-9325-053ad1c5dbe3" \
  "d8a12fa2-d432-402f-8ee3-2fa3cf7ad242" \
  "25dc651f-ea5c-4e74-8294-f13d260046cc" \
  "e7e04673-cd58-4e82-85bf-15693ab6b739" \
  "3080ded2-aed9-452b-8c5f-566c9b4cc628" \
  "f06fbfc1-c7bb-430a-b293-df031a7e7a1c" \
  "9df5d57f-805b-44bf-8d78-846d8fd6e891" \
  "198a0c85-33de-40b8-aef0-4d716d31d50b" \
  "18674e94-dffc-41f0-9eca-dbb5ffdc4f63" \
  "840b6b5f-83e8-42b4-b8d8-7c4b9dde3b2d" \
  "92c5cd07-b998-4b2e-a5b6-e56ae3b17365" \
  "c9548366-d782-4383-9889-ce2dcb65cba6" \
  "c653553b-c4fb-46fe-b641-d1d722d79946" \
  "2a019583-a042-4934-a697-22e5845d08b3" \
  "4cba3de7-d104-48bf-ac60-91cd808d19f7" \
  "0336c05f-908e-4e26-adc5-8fbf4f0963ae" \
  "5bf8d3b7-dcfc-403a-ab67-f3518839cdf1" \
  "f2eced3e-c6b3-4df5-9753-3ad24d9d5354" \
  "13605079-4a91-45b6-a164-adbf990f09e9" \
  "1d4d3f62-0fe2-4e12-8985-48df80fb592f" \
  "fb63b8e9-ee48-4b3e-98dc-384da4122d72" \
  "fb67d149-516a-4914-a52e-4d4e12d79690" \
  "f9bc0e70-c045-4dea-8227-dcc0df7b3918"
do
  curl "https://2023.confitura.pl/api/presentations/$id?projection=inlineSpeaker" > "$id.json"
done