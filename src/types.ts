export interface TopUpPackage {
  id: string;
  name: string;
  commandValue: string;
  isCustom?: boolean;
}

export interface PlayerVerification {
  account: string;
  open_id: string;
}
