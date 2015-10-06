namespace py PyInterface

struct SentiRequestObject {
  1: required string mainText;
  2: optional string textType = "microblogs";
  3: optional string title = "";
  4: optional string middleParas = "";
  5: optional string lastPara = "";
  6: optional string topDomain = "";
  7: optional string subDomain = "";
  }

service PyInterface {
  void ping(),
  i32 getSentimentScore(1: SentiRequestObject obj),
  list<string> getTopics(1:string msg),
  list<string> getKeywords(1:string msg),
  string ExAcro(1:string input),
  string ExEmo(1:string input),
}
